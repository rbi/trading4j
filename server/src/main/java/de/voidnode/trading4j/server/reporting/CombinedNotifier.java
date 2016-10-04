package de.voidnode.trading4j.server.reporting;

/**
 * A notifier that can inform all groups of persons.
 * 
 * @author Raik Bieniek
 */
public interface CombinedNotifier extends TraderNotifier, AdmininstratorNotifier, DeveloperNotifier {

}
