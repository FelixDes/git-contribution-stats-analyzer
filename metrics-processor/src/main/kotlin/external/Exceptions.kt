package external

class FileNotFoundInIndexException(path: Path) : RuntimeException("File for path $path was not found in index")

class UserNotFoundInIndexException(user: User) : RuntimeException("User with email ${user.email} was not found in index")
