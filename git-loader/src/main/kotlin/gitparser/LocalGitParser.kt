package gitparser

import external.DomainTreeDto
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectReader
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.TreeWalk
import java.io.File

data object LocalGitParser {
    fun parse(dir: File): DomainTreeDto? {
        val repo = FileRepository(dir)
        val reader: ObjectReader = repo.newObjectReader()

        val walk = RevWalk(repo)
        val topCommit = walk.parseCommit(repo.resolve(Constants.HEAD))
        val treeWalk = TreeWalk(repo)
        treeWalk.addTree(topCommit.tree)
        treeWalk.isRecursive = true

        while (treeWalk.next()) {
            repo.open(treeWalk.getObjectId(0)).copyTo(System.out)
            if (treeWalk.isSubtree) {
                treeWalk.enterSubtree()
                continue
            }
        }

        return null
    }
}