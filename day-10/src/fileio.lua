require "io"

local FileIO = {} -- FileIO module
function FileIO.testFileExists(path)
    local f = io.open(path, "r")
    if f ~= nil then
        io.close(f)
        return true
    else
        return false
    end
end

function FileIO.readFile(path)
    if not FileIO.testFileExists(path) then
        return {}
    end
    local lines = {}
    for line in io.lines(path) do
        lines[#lines + 1] = line
    end
    return lines
end

return FileIO
