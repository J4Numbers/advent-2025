local lu = require "luaunit"
local lfs = require "lfs"
local fileIO = require "src/fileio"

local dir = lfs.currentdir()

TestFileIO = {}

function TestFileIO:testFileExistsWhenItDoes()
    lu.assertEquals(fileIO.testFileExists("./inputs/blank.txt"), true)
end

function TestFileIO:testFileExistsWhenItDoesNot()
    lu.assertEquals(fileIO.testFileExists("./inputs/missing.txt"), false)
end

function TestFileIO:testFileReadReturnsContents()
  lu.assertEquals(fileIO.readFile("./inputs/simple-file.txt"), {"abc", "def"})
end

return TestFileIO
