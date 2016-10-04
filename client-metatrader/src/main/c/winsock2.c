#include <stdio.h>
#include <stdint.h>

#include <winsock2.h>
#include <ws2tcpip.h>
#include <windows.h>

#define VN_IMPORT __declspec( dllimport )
#define VN_EXPORT __declspec( dllexport )

#define byte unsigned char

#define vnConnection int
#define vnBuffer void *

///////////////////////
/// private methods ///
///////////////////////

SOCKET vnTryConnect(struct addrinfo *remoteAddress) {
	SOCKET connectSocket = INVALID_SOCKET;
	connectSocket = socket(remoteAddress->ai_family, remoteAddress->ai_socktype,
			remoteAddress->ai_protocol);

	if (connectSocket == INVALID_SOCKET) {
		printf(
				"Can't create a socket for the connection to the server. method: socket(), error: %ld\n",
				WSAGetLastError());
		return connectSocket;
	}

	int returnVal = connect(connectSocket, remoteAddress->ai_addr,
			(int) remoteAddress->ai_addrlen);
	if (returnVal == SOCKET_ERROR) {
		printf("Can't connect to server! method: connect(), error: %ld\n",
				WSAGetLastError());
		closesocket(connectSocket);
		return INVALID_SOCKET;
	}
	return connectSocket;
}

void vnRecieveFully(SOCKET socket, byte * buffer, int readCount) {
	int bytesReadAbsolute = 0;

	while (bytesReadAbsolute < readCount) {
		int bytesRead = recv(socket, buffer + bytesReadAbsolute,
				readCount - bytesReadAbsolute, 0);
		if (bytesRead == SOCKET_ERROR) {
			printf(
					"Can't read data from the server. method: recv(), error: %d\n",
					WSAGetLastError());
			closesocket(socket);
			WSACleanup();
			return;
		} else if (bytesRead == 0) {
			printf("The socket has been closed. method: recv(), error: %d\n",
					WSAGetLastError());
			memset(buffer, 0, readCount - bytesReadAbsolute);
			closesocket(socket);
			WSACleanup();
			return;
		}
		bytesReadAbsolute += bytesRead;
	}
}

void vnHton16bit(byte * inputBytes, byte * outputBytes) {
	int i;
	for (i = 0; i < 2; i++) {
		outputBytes[i] = inputBytes[1 - i];
	}
}

void vnHton32bit(byte * inputBytes, byte * outputBytes) {
	int i;
	for (i = 0; i < 4; i++) {
		outputBytes[i] = inputBytes[3 - i];
	}
}

void vnHton64bit(byte * inputBytes, byte * outputBytes) {
	int i;
	for (i = 0; i < 8; i++) {
		outputBytes[i] = inputBytes[7 - i];
	}
}

/////////////////////////////
/// connection management ///
/////////////////////////////

/**
 * Connects to a remote TCP socket.
 *
 * @param host The host to connect to.
 * @param port The port at the host to connect to.
 * @returns If > 0: a handle representing the connection, else: an error code
 */
VN_EXPORT vnConnection vnConnect(const char * host, uint16_t port) {
	WSADATA wsaData;
	if (WSAStartup(MAKEWORD(2, 0), &wsaData) != 0) {
		printf(
				"Can't initialize winsock2 library. method: WSAStartup(), error: %d\n",
				WSAGetLastError());
		return -1;
	}

    //based on http://msdn.microsoft.com/de-de/library/windows/desktop/bb530741%28v=vs.85%29.aspx
	char portString[6];
	sprintf(portString, "%d", port);

	struct addrinfo *allRemoteAddress = NULL, *nextRemoteAddress = NULL, hints;

	ZeroMemory(&hints, sizeof(hints));
	hints.ai_family = AF_UNSPEC;
	hints.ai_socktype = SOCK_STREAM;
	hints.ai_protocol = IPPROTO_TCP;

	int returnVal = getaddrinfo(host, portString, &hints, &allRemoteAddress);
	if (returnVal != 0) {
		printf(
				"Can't resolve the server address. method: getaddrinfo(), error: %d\n",
				returnVal);
		WSACleanup();
		return -1;
	}

	nextRemoteAddress = allRemoteAddress;
	SOCKET connectSocket = INVALID_SOCKET;

	for (nextRemoteAddress = allRemoteAddress; nextRemoteAddress != NULL;
			nextRemoteAddress = nextRemoteAddress->ai_next) {
		connectSocket = vnTryConnect(nextRemoteAddress);
		if (connectSocket != INVALID_SOCKET) {
			break;
		}
		if (nextRemoteAddress->ai_next != NULL) {
			printf("Trying next server address.\n");
		} else {
			printf("There are no more server addresses to try.\n");
			WSACleanup();
		}
	}

	freeaddrinfo(allRemoteAddress);

	if (connectSocket == INVALID_SOCKET) {
		return -1;
	}
	return connectSocket;
}

/**
 * Disconnects from a remote TCP socket.
 *
 * @param connection The handle representing
 * @returns 0 if successful and else an error code
 */
VN_EXPORT int vnDisconnect(const vnConnection connection) {
	SOCKET socket = connection;
	closesocket(socket);
	WSACleanup();
	return 0;
}

///////////////
/// reading ///
///////////////

/**
 * Reads a single byte of data from a TCP socket.
 *
 * @param connection The connection to read the data from.
 * @returns the read byte
 */
VN_EXPORT byte vnReadByte(const int connection) {
	SOCKET socket = connection;
	byte buffer;
	vnRecieveFully(socket, &buffer, 1);
	return buffer;
}

/**
 * Reads a signed 32bit integer in the network byte order.
 *
 * @param connection The connection to read the data from.
 * @returns the read integer
 */
VN_EXPORT int32_t vnReadInt32(const int connection) {
	SOCKET socket = connection;
	byte input[4];
	int32_t output;

	vnRecieveFully(socket, input, sizeof(input));
	vnHton32bit(input, (byte *) &output);

	return output;
}

/**
 * Reads a signed 64bit integer in the network byte order.
 *
 * @param connection The connection to read the data from.
 * @returns the read integer
 */
VN_EXPORT int64_t vnReadInt64(const int connection) {
	SOCKET socket = connection;
	byte input[8];
	int64_t output;

	vnRecieveFully(socket, input, sizeof(input));
	vnHton64bit(input, (byte *) &output);

	return output;
}

/**
 * Reads a double value in the network byte order.
 *
 * @param connection The connection to read the data from.
 * @returns the read double
 */
VN_EXPORT double vnReadDouble(const int connection) {
	int64_t data = vnReadInt64(connection);
	return *((double *) ((byte *) &data));
}

///////////////
/// writing ///
///////////////

/**
 * Writes a double to a buffer converted to the network byte order.
 *
 * @param buffer The buffer to write to
 * @param offset An offset in bytes from the start of the buffer where to write
 * @param doubleToWrite The dobule value that should be written to the buffer.
 */
VN_EXPORT void vnWriteConvertedDoubleToBuffer(const vnBuffer buffer, const int offset, const double doubleToWrite) {
	vnHton64bit((byte *) &doubleToWrite, (byte *)(buffer + offset));
}

/**
 * Writes a byte to a buffer.
 *
 * @param buffer The buffer to write to
 * @param offset An offset in bytes from the start of the buffer where to write
 * @param byteToWrite The byte that should be written to the buffer.
 */
VN_EXPORT void vnWriteByteToBuffer(const vnBuffer buffer, const int offset, const byte byteToWrite) {
	((byte *)buffer)[offset] = byteToWrite;
}

/**
 * Writes an int64 to a buffer converted to the network byte order.
 *
 * @param buffer The buffer to write to
 * @param offset An offset in bytes from the start of the buffer where to write
 * @param int64ToWrite The int64 value that should be written to the buffer.
 */
VN_EXPORT void vnWriteConvertedInt64ToBuffer(const vnBuffer buffer, const int offset, const int64_t int64ToWrite) {
	vnHton64bit((byte *) &int64ToWrite, (byte *)(buffer + offset));
}

/**
 * Writes an int32 to a buffer converted to the network byte order.
 *
 * @param buffer The buffer to write to
 * @param offset An offset in bytes from the start of the buffer where to write
 * @param int32ToWrite The int32 value that should be written to the buffer.
 */
VN_EXPORT void vnWriteConvertedInt32ToBuffer(const vnBuffer buffer, const int offset, const int32_t int32ToWrite) {
	vnHton32bit((byte *) &int32ToWrite, (byte *)(buffer + offset));
}

/**
 * Writes an uint16 to a buffer converted to the network byte order.
 *
 * @param buffer The buffer to write to
 * @param offset An offset in bytes from the start of the buffer where to write
 * @param uint16ToWrite The uint16 value that should be written to the buffer.
 */
VN_EXPORT void vnWriteConvertedUInt16ToBuffer(const vnBuffer buffer, const int offset, const uint16_t uint16ToWrite) {
	vnHton16bit((byte *) &uint16ToWrite, (byte *)(buffer + offset));
}

/**
 * Writes a zero terminated string with it length to a buffer.
 *
 * <p>The length of the string (meaning its number of bytes) is prepended to the buffer as an uint16.
 * The terminating zero will not be written</p>
 * @param buffer The buffer to write to
 * @param offset An offset in bytes from the start of the buffer where to write
 * @param stringToWrite The string that should be written to the buffer.
 */
VN_EXPORT void vnWriteStringToBuffer(const vnBuffer buffer, const int offset, const wchar_t * stringToWrite) {
	int requieredLength = WideCharToMultiByte(CP_UTF8, 0, stringToWrite, -1, NULL, 0, NULL, NULL);
	char utf8String[requieredLength];
	WideCharToMultiByte(CP_UTF8, 0, stringToWrite, -1, utf8String, requieredLength, NULL, NULL);

	vnWriteConvertedUInt16ToBuffer(buffer, offset, requieredLength - 1);
	memcpy((void *)(buffer + offset + 2), utf8String, requieredLength - 1);
}

/////////////////////////
/// buffer management ///
/////////////////////////

/**
 * Allocates a new buffer
 *
 * @param length The size of the buffer to allocate
 * @returns A pointer to the buffer
 */
VN_EXPORT vnBuffer vnAllocateBuffer(const int length) {
	return malloc(length);
}

/**
 * Writes a byte buffer to a TCP socket.
 *
 * @param connection The connection to write data to.
 * @param buffer The buffer to write
 * @param length The amount of data to write from the start of the buffer
 * @returns 0 if successful and else an error code
 */
VN_EXPORT int vnWriteBuffer(const vnConnection connection, const byte * buffer,
		const int length) {
	SOCKET socket = connection;
	int resultValue = send(socket, buffer, length, 0);
	if (resultValue == SOCKET_ERROR) {
		printf("Can't send data to the server. method: send(), error: %d\n",
				WSAGetLastError());
		closesocket(socket);
		WSACleanup();
		return 1;
	}
	return 0;
}

/**
 * Deallocates a buffer previously allocated by vnAllocateBuffer.
 *
 * @param buffer The buffer to deallocate.
 */
VN_EXPORT void vnDeallocateBuffer(vnBuffer buffer) {
	free(buffer);
}
