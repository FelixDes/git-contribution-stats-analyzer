package gus.external

class InvalidRepoLinkException(link: String) : RuntimeException("Link $link is not valid git repository url/path")