package org.kobjects.sugarcoat.testsources
val FIZZBUZZ_CASE = arrayOf("FizzBuzz", """
fn main()
   for (range(1, 20)) :: x
     if (x % 3 == 0) 
       print("Fizz")
       
       if (x % 5 == 0)
         print("Buzz")
     
     --elif (x % 5 == 0)
       print("Buzz")
     
     --else
       print(x)
     
     print("\n")    
""", """
1
2
Fizz
4
Buzz
Fizz
7
8
Fizz
Buzz
11
Fizz
13
14
FizzBuzz
16
17
Fizz
19
""")