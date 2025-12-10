rockspec_format = "3.0"
package = "day-10"
version = "1.0.0-1"
source = {
   url = "git+ssh://git@github.com/J4Numbers/advent-2025.git"
}
description = {
   homepage = "https://github.com/J4Numbers/advent-2025",
   license = "0BSD"
}

dependencies = {
   "lua ~> 5.4"
}
test_dependencies = {
  "luaunit >= 3.4",
  "luafilesystem >= 1.8"
}

build = {
   type = "builtin",
   modules = {
      main = "src/main.lua"
   }
}

test = {
  type = "command",
  command = "lua test/test.lua"
}
