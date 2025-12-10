local TestDoThing = require "test/main_spec"
local TestFileIO = require "test/fileio_spec"

local lu = require "luaunit"
os.exit(lu.LuaUnit.run())
