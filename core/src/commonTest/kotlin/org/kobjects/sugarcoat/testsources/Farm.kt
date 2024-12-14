package org.kobjects.sugarcoat.testsources; val FARM_CASE = arrayOf("Farm", """

trait Animal 
  fn sound() -> String

struct Sheep
  name: String
  
impl Animal for Sheep
  fn sound() -> String
     "maeh"
     
fn main() 
  let animal = Sheep.create("Dolly") as Animal
  print(animal.sound())

""","""
maeh
""")