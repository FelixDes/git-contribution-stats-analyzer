package gus.external

import gus.external.Path
import gus.external.User

class FileNotFoundInIndexException(path: Path) : RuntimeException("File for path $path was not found in index")

class UserNotFoundInIndexException(user: User) : RuntimeException("User with email ${user.email} was not found in index")

class NotEnoughUsersToComputePair : RuntimeException("Not enough users to compute a pair")
