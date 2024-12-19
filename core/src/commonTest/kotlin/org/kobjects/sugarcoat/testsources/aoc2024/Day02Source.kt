package org.kobjects.sugarcoat.testsources.aoc2024;  val DAY_02_SOURCE = """

fn main(input: String) -> I64
  let maxUnsafe = 0
  let mut result = 0
  let lines = input.split("\n")
  for (range(0, lines.size)) :: i
    let line = lines[i]
    let mut previousDelta = 0
    let numbers = line.split(" ")
    let mut unsafe = 0
    for (range(1, numbers.size)) :: j
      let delta = I64.parse(numbers[j]) - I64.parse(numbers[j - 1])
      if (delta == 0 || delta < -3 || delta > 3 || (previousDelta != 0 && delta < 0 != previousDelta < 0))
        unsafe = unsafe + 1
            
      previousDelta = delta
      
    if (unsafe <= maxUnsafe) 
      result = result + 1
    
  result
"""