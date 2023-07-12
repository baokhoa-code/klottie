#----------------------------------------------------------------
# Generated CMake target import file for configuration "Debug".
#----------------------------------------------------------------

# Commands may need to know the format version.
set(CMAKE_IMPORT_FILE_VERSION 1)

# Import target "rlottie::rlottie" for configuration "Debug"
set_property(TARGET rlottie::rlottie APPEND PROPERTY IMPORTED_CONFIGURATIONS DEBUG)
set_target_properties(rlottie::rlottie PROPERTIES
  IMPORTED_LOCATION_DEBUG "/usr/lib/librlottie.so"
  IMPORTED_SONAME_DEBUG "librlottie.so"
  )

list(APPEND _IMPORT_CHECK_TARGETS rlottie::rlottie )
list(APPEND _IMPORT_CHECK_FILES_FOR_rlottie::rlottie "/usr/lib/librlottie.so" )

# Commands beyond this point should not need to know the version.
set(CMAKE_IMPORT_FILE_VERSION)
