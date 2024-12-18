package org.kobjects.sugarcoat.testsources.aoc2024;  val DAY_02_SOURCE = """

fn main(input: String) -> I64
  let mut result = 0
  let lines = input.split("\n")
  for (range(0, lines.size)) :: i
    let line = lines[i]
    print ("\n\n", line, "\n")
    let mut previousDelta = 0
    let numbers = line.split(" ")
    let mut safe = true
    for (range(1, numbers.size)) :: j
      let delta = I64.parse(numbers[j]) - I64.parse(numbers[j - 1])
      print (delta)
      if (delta == 0 || delta < -3 || delta > 3) 
        safe = false
        print ("Unsafe because range")
        
      if (previousDelta != 0 && delta < 0 != previousDelta < 0) 
        safe = false
        print ("Unsafe b/c direction change")
    
      previousDelta = delta
      
    if (safe) 
      result = result + 1
    
    print("result", result.toString(), safe.toString())
  result
"""