const val MAX_SIZE_PART_1 = 100000
const val MAX_USED_SPACE = 70000000 - 30000000

fun main() {
    fun getListPerDirectory(input: List<String>): Map<String, List<String>> {
        val listPerDirectory = mutableMapOf<String, MutableList<String>>()
        var currentDirectory = ""
        for (i in input.indices) {
            val command = input[i].split(" ")
            when (command[0]) {
                "$" -> {
                    if (command[1] == "cd") {
                        // Change directory
                        if (command[2] == "..") {
                            currentDirectory = currentDirectory.substringBeforeLast("/")
                        } else {
                            currentDirectory += "/" + command[2]
                        }

                        // Check if the map already contains the new currentDirectory.
                        // If not, add it to create a list and avoid null exceptions later on.
                        if (!listPerDirectory.containsKey(currentDirectory)) {
                            listPerDirectory[currentDirectory] = mutableListOf()
                        }
                    }
                }
                "dir" -> {
                    // Add child directory to the list of the files within the currentDirectory.
                    listPerDirectory[currentDirectory]!!.add(currentDirectory + "/" + command[1])
                }
                else -> {
                    // Add file size to the list of the files within the currentDirectory.
                    listPerDirectory[currentDirectory]!!.add(command[0])
                }
            }
        }
        return listPerDirectory
    }

    fun calculateDirectorySize(listPerDirectory: Map<String, List<String>>, directory: String): Long {
        var sum = 0.toLong()
        for (line in listPerDirectory[directory]!!) {
            sum += line.toLongOrNull() ?: calculateDirectorySize(listPerDirectory, line)
        }
        return sum
    }

    fun getSizePerDirectory(listPerDirectory: Map<String, List<String>>): Map<String, Long> {
        val sizePerDirectory = mutableMapOf<String, Long>()
        for (directory in listPerDirectory.keys) {
            sizePerDirectory[directory] = calculateDirectorySize(listPerDirectory, directory)
        }
        return sizePerDirectory
    }

    fun part1(input: List<String>): Long {
        val listPerDirectory = getListPerDirectory(input)
        val sizePerDirectory = getSizePerDirectory(listPerDirectory)
        return sizePerDirectory.values.filter{it <= MAX_SIZE_PART_1}.sum()
    }

    fun part2(input: List<String>): Long {
        val listPerDirectory = getListPerDirectory(input)
        val sizePerDirectory = getSizePerDirectory(listPerDirectory)
        val sizeToDelete = sizePerDirectory.values.first() - MAX_USED_SPACE
        return sizePerDirectory.values.sorted().first { it >= sizeToDelete }
    }

    val input = readInputAsStrings("Day07")

    println(part1(input))
    println(part2(input))
}
