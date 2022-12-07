fun main() {
    fun calculateDirectorySize(listPerDirectory: Map<String, List<String>>, directory: String): Long {
        var sum = 0.toLong()
        for (line in listPerDirectory[directory]!!) {
            val fileSize = line.toLongOrNull()
            if (fileSize != null) {
                sum += fileSize
            } else {
                sum += calculateDirectorySize(listPerDirectory, line)
            }
        }
        return sum
    }

    fun part1(input: List<String>): Long {
        val listPerDirectory = mutableMapOf<String, MutableList<String>>()
        var currentDirectory = ""
        for (i in input.indices) {
            val splitLine = input[i].split(" ")
            when (splitLine.first()) {
                "$" -> {
                    if (splitLine[1] == "cd") {
                        when (splitLine.last()) {
                            ".." -> {
                                currentDirectory = currentDirectory.substringBeforeLast("/")
                                if (!listPerDirectory.containsKey(currentDirectory)) {
                                        listPerDirectory[currentDirectory] = mutableListOf()
                                    }
                            }
                            else -> {
                                currentDirectory += "/" + splitLine.last()
                                if (!listPerDirectory.containsKey(currentDirectory)) {
                                        listPerDirectory[currentDirectory] = mutableListOf()
                                    }
                            }
                        }
                    }
                }
                "dir" -> {
                    listPerDirectory[currentDirectory]!!.add(currentDirectory + "/" + splitLine[1])
                }
                else -> {
                    listPerDirectory[currentDirectory]!!.add(splitLine.first())
                }
            }
        }

        val sizePerDirectory = mutableMapOf<String, Long>()
        for (directory in listPerDirectory.keys) {
            sizePerDirectory[directory] = calculateDirectorySize(listPerDirectory, directory)
        }

        return sizePerDirectory.values.filter{it <= 100000}.sum()
    }

    fun part2(input: List<String>): Long {
        val listPerDirectory = mutableMapOf<String, MutableList<String>>()
        var currentDirectory = ""
        for (i in input.indices) {
            val splitLine = input[i].split(" ")
            when (splitLine.first()) {
                "$" -> {
                    if (splitLine[1] == "cd") {
                        when (splitLine.last()) {
                            ".." -> {
                                currentDirectory = currentDirectory.substringBeforeLast("/")
                                if (!listPerDirectory.containsKey(currentDirectory)) {
                                    listPerDirectory[currentDirectory] = mutableListOf()
                                }
                            }
                            else -> {
                                currentDirectory += "/" + splitLine.last()
                                if (!listPerDirectory.containsKey(currentDirectory)) {
                                    listPerDirectory[currentDirectory] = mutableListOf()
                                }
                            }
                        }
                    }
                }
                "dir" -> {
                    listPerDirectory[currentDirectory]!!.add(currentDirectory + "/" + splitLine[1])
                }
                else -> {
                    listPerDirectory[currentDirectory]!!.add(splitLine.first())
                }
            }
        }

        val sizePerDirectory = mutableMapOf<String, Long>()
        for (directory in listPerDirectory.keys) {
            sizePerDirectory[directory] = calculateDirectorySize(listPerDirectory, directory)
        }

        val sizeToDelete = sizePerDirectory["//"]!! - (70000000-30000000)
        return sizePerDirectory.values.sorted().filter{it >= sizeToDelete}.first()
    }

    val input = readInputAsStrings("Day07")

    println(part1(input))
    println(part2(input))
}
