package gus.external

class InvalidRepoLinkException(link: String) : RuntimeException("Link '$link' is not valid git repository url/path. It should look like https://github.com/user/repo.git or /home/user/repo.git")