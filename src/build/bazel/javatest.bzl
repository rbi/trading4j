"""Generates a single java_test rules for each .java file passed in srcs"""

def gen_java_test_rules(name, srcs, **kwargs):
  for test_file in srcs:
    if not test_file.endswith(".java"):
      fail("Only Java source files may be used.", test_file)
    test_name = test_file[:-5]
    test_class = _path_to_java_class(PACKAGE_NAME + "/" + test_name)
    native.java_test(name = test_name,
                   test_class = test_class,
                   srcs = [test_file],
                   **kwargs)

def _path_to_java_class(path):
  package_start = _find_package_start_index(path)
  return path[package_start:].replace('/', '.')

def _find_package_start_index(path):
  test_folder = "java/"
  index = path.find(test_folder)
  if index < 0:
    fail("Test rule generation only works if the tests are localed in a java/ folder.", path)
  return index + len(test_folder)
