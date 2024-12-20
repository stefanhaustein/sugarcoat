package org.kobjects.sugarcoat.testsources; val COMPLEX_CASE = arrayOf("Complex", """
struct Complex
  a: F64
  b: F64
  
  fn abs() -> F64
    sqrt(a*a + b*b)

fn main() 
  let c: Complex = Complex.create(1.0, 2.0)     
  print(c.abs.toString())    
""",
"2.23606797749979")