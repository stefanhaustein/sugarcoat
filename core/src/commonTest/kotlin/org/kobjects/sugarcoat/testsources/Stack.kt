package org.kobjects.sugarcoat.testsources; val STACK_CASE = arrayOf("StackTest", """
struct Stack[T]
  data: MutableList[T]
  
  fn push(value: T)
    data.add(value)
    
  fn pop() -> T
    let index = data.size - 1
    let result = data[index]
    data.remove(index)
    result

fn main() -> I64
  let s = Stack[I64]
  s.push(42)
  s.pop
""",
    "")