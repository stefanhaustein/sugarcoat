package org.kobjects.sugarcoat.testsources
val RAYTRACER_SOURCE = """

import graphics.Color

struct Vector
  x: F64
  y: F64
  z: F64

  fn times(k: F64) -> Vector
    Vector(k * x, k * y, k * z)

  fn minus(v2: Vector) -> Vector
    Vector(x - v2.x, y - v2.y, z - v2.z)

  fn plus(v2: Vector) -> Vector
    Vector(x + v2.x, y + v2.y, z + v2.z)

  fn dot(v2: Vector) -> F64
    x * v2.x + y * v2.y + z * v2.z

  fn mag() -> F64
    sqrt(x * x + y * y + z * z)

  fn norm() -> Vector
    times(1.0 / mag())

  fn cross(v2: Vector) -> Vector
    Vector(y * v2.z - z * v2.y, z * v2.x - x * v2.z, x * v2.y - y * v2.x)

struct Camera
  pos: Vector
  forward: Vector
  right: Vector
  up: Vector

  static fn lookingAt(pos: Vector, lookAt: Vector) -> Camera
    let down = Vector(0, -1, 0)
    let forward = lookAt.minus(pos).norm()
    let right = forward.cross(down).norm().times(1.5)
    let up = forward.cross(right).norm().times(1.5)
    Camera(pos, forward, right, up)

  struct Ray
    start: Vector
    dir: Vector

  struct Intersection
    thing: Thing
    ray: Ray
    dist: F64

  trait Surface
    fn roughness() -> F64
    fn diffuse(pos: Vector) -> Color
    fn specular(pos: Vector) -> Color
    fn reflect(pos: Vector) -> F64

  trait Thing
    fn surface() -> Surface
    fn normal(pos: Vector) -> Vector
    fn intersect(ray: Ray) -> Intersection

  struct Light
    pos: Vector
    color: Color

  struct Scene
    things: List<Thing>
    lights: List<Light>
    camera: Camera
    background: Color

  struct Sphere
    center: Vector
    radius2: F64
    surface: Surface

  static fn create(center: Vector, radius: F64, surface: Surface) -> Sphere
    Sphere(center, radius * radius, surface)

  impl Thing for Sphere
    fn surface() -> Surface
      surface

    fn normal(pos: Vector) -> Vector
      pos.minus(center).norm()

    fn intersect(r: Ray) -> Intersection
      let eo = center.minus(r.start)
      let v = eo.dot(r.dir)
      let mut dist = 1/0
      if (v >= 0)
        let disc = radius2 - (eo.dot(eo) - v * v)
        if (disc >= 0)
          dist = v - sqrt(disc)

      Intersection(self, r, dist)

struct Plane
  norm: Vector
  offset: F64
  surface: Surface

impl Thing for Plane
  fn surface() -> Surface
    surface

  fn normal(pos: Vector) -> Vector:
    norm

  fn intersect(r: Ray) -> Intersection:
    let denom = norm.dot(r.dir)
    let mut dist = 1/0
    if (denom <= 0)
      dist = (norm.dot(r.start) + offset) / -denom

    Intersection(self, r, dist)


object Shiny

impl Surface for Shiny:
  fn roughness() -> F64
    250

  fn diffuse(pos: Vector) -> Color
    Color.WHITE

  fn specular(pos: Vector) -> Color
    Color.GRAY

  fn reflect(pos: Vector) -> F64
    0.7

object Checkerboard

impl Surface for Checkerboard:
  fn roughness() -> F64
    150

  fn diffuse(pos: Vector) -> Color
    if ((int(1000 + pos.z) + int(1000 + pos.x)) % 2 != 0) 
      Color.WHITE
    --else
      Color.BLACK

  fn specular(pos: Vector) -> Color
    Color.WHITE

  fn reflect(pos: Vector) -> F64
    if ((int(1000 + pos.z) + int(1000 + pos.x)) % 2 != 0) 
      0.1
    --else
      0.7

struct RayTracer
  maxDepth = 5
  defaultColor = Color.BLACK

  fn intersections(r: Ray, s: Scene) -> Intersection
    let mut closest = s.things[0].intersect(r)
    for (range(1, len(s.things))) :: i
      let inter = s.things[i].intersect(r)
      if (inter.dist < closest.dist) 
        closest = inter

    closest

  fn testRay(r: Ray, s: Scene) -> F64
    intersections(r, s).dist

  fn traceRay(r: Ray, s: Scene, depth: F64) -> Color
    let isect = intersections(r, s)
    if (isect.dist == 1/0)
      s.background
    --else
      shade(isect, s, depth)

  fn shade(isect: Intersection, s: Scene, depth: F64) -> Color
    let d = isect.ray.dir
    let pos = d.times(isect.dist).plus(isect.ray.start)
    let normal = isect.thing.normal(pos)
    let reflectDir = d.minus(normal.times(2 * normal.dot(d)))
    let naturalColor = s.background.plus(getNaturalColor(isect.thing, pos, normal, reflectDir, s))
    let mut reflectedColor = Color.GRAY
    if (depth < maxDepth)
      reflectedColor = getReflectionColor(isect.thing, pos, normal, reflectDir, s, depth)

    naturalColor.plus(reflectedColor)

  fn getReflectionColor(t: Thing, pos: Vector, normal: Vector, rd: Vector, s: Scene, depth: F64) -> Color
    traceRay(Ray(pos, rd), s, depth + 1).scale(t.surface().reflect(pos))

  fn addLight(t: Thing, pos: Vector, norm: Vector, rd: Vector, s: Scene, col: Color, l: Light) -> Color
    let ldis = l.pos.minus(pos)
    let livec = ldis.norm()
    let nearIsect = testRay(Ray(pos, livec), s)
    let isInShadow = (nearIsect <= ldis.mag())
    if (isInShadow) 
      return col

    let illum = livec.dot(norm)
    let mut lcolor = defaultColor
    if (illum > 0)
      lcolor = l.color.scale(illum)

    let specular = livec.dot(rd.norm())
    let mut scolor = defaultColor
    if (specular > 0)
      scolor = l.color.scale(specular ** t.surface().roughness())

    let surf = t.surface()
    let diff = surf.diffuse(pos)
    col.plus(lcolor.times(diff)).plus(scolor.times(t.surface().specular(pos)))

  fn getNaturalColor(t: Thing, pos: Vector, norm: Vector, rd: Vector, s: Scene) -> Color
    let mut col = defaultColor
    for (s.lights) :: l
      col = addLight(t, pos, norm, rd, s, col, l)

    col

  fn getPoint(x: F64, y: F64, cam: Camera) -> Vector
    cam.forward.plus(cam.right.times(x / 200)).plus(cam.up.times(y / 200)).norm()

  fn render(s: Scene, b: graphics.BitmapImage) 
    let y0 = b.height // 2
    let scale = 150.0 / min(b.width, b.height)
    renderRow(s, b, scale, y0)
    for (range(0, y0)) :: y
      renderRow(s, b, scale, y0 - y)
      renderRow(s, b, scale, y0 + y)

  fn renderRow(s: Scene, b: graphics.BitmapImage, scale: F64, y: int)
    let cx = b.width//2
    let cy = b.height//2
    for (range(0, b.width)) :: x
      let color = traceRay(Ray(s.camera.pos, getPoint((x - cx) * scale, (cy - y) * scale, s.camera)), s, 0)
      b.set(x, y, color)

defaultThings = [Plane(Vector(0,1,0), 0, Checkerboard) as Thing, Sphere(Vector(0,1,-0.25), 1, Shiny) as Thing, Sphere(Vector(-1,0.5,1.5),0.5, Shiny) as Thing]
defaultLights = [Light(Vector(-2,2.5,0), Color(0.49,0.07,0.07)), Light(Vector(1.5,2.5,1.5), Color(0.07,0.07,0.49)), Light(Vector(1.5,2.5,-1.5), Color(0.07,0.49,0.071)), Light(Vector(0,3.5,0), Color(0.21,0.21,0.35))]
defaultCamera = Camera.lookingAt(Vector(3,2,4), Vector(-1,0.5,0))
defaultScene = Scene(defaultThings, defaultLights, defaultCamera, Color.BLACK)

fn main()
  let rayTracer = RayTracer()
  rayTracer.render(defaultScene, screen.image)
"""