local lu = require "luaunit"
local doThing = require "src/main"

TestDoThing = {}

function TestDoThing:testHelloWorld()
    lu.assertEquals(doThing("hello"), "Hello world!")
end

function TestDoThing:testGoodbyeParadise()
    lu.assertEquals(doThing("goodbye"), "Goodbye paradise!")
end

return TestDoThing
