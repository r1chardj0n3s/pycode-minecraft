===================
Python in Minecraft
===================

I wanted to code Python stuff in Minecraft, so I smooshed jython
in and added some extra stuff.


.. image:: screenshot/editor.png

Install using the download from https://minecraft.curseforge.com/projects/python-code


What you can do
===============

Once you've created a Python Block or Hand you can write some Python
code in a Writeable Book (or the superior Python Code Book), right click
it on a the Python Hand or Block and have that code be run.

If you want to delay the running of your code you can put all the code
in a run() function. That function will then be run only when you right
click the Python Wand on the hand or block.

Also, Python Blocks may have other events that they handle:

Redstone Power

   If you define a powerOn() or powerOff() function these will be called
   when the block receives, or loses redstone power (eg from redstone
   wiring, or just a button placed on the block.)

Time Passsing

   If you define a tick() function this will be called about 20 times
   per second.



Recipes
-------

Wand
~~~~

Wands are used to create Python Code Books and interact
with Python Blocks and Python Hands.

+------------+-------------+------------+
|            |             | Lapis      |
+------------+-------------+------------+
|            | Redstone    | Yellow Dye |
+------------+-------------+------------+
| Stick      |             |            |
+------------+-------------+------------+

Python Block
~~~~~~~~~~~~

Python blocks sit in the world and can do stuff.

+-------------+-------------+------------+
| Cobblestone | Lapis       | Cobblestone|
+-------------+-------------+------------+
| Lapis       | Redstone    | Yellow Dye |
+-------------+-------------+------------+
| Cobblestone | Yellow Dye  | Cobblestone|
+-------------+-------------+------------+

Hand
~~~~

Python hands can move around in the world and can do stuff.

+-------------+-------------+------------+
|             | Lapis       |            |
+-------------+-------------+------------+
| Wool        | Redstone    | Yellow Dye |
+-------------+-------------+------------+
|             | Wool        |            |
+-------------+-------------+------------+

Python Code Book
~~~~~~~~~~~~~~~~

Craft a Python Wand with a Writeable Book. It's a superior
editable book that will also check your Python code for
syntax errors.


Hand OR Block
-------------

::

    block.chat(“message”)

    block.world - the world object
    block.pos - the BlockPos
    block.player - the activating player
    block.blocks - Blocks.class
    block.items - Items.class
    block.entities - EnumEntities.class

    block.water(pos.up())
    block.lava(pos.left())
    block.clear(pos.left())


Event Handlers
~~~~~~~~~~~~~~

::

  def run():
    # invoked when activated with the wand


Block
-----

:: 

    block.powered (boolean)

    block.firework()

    block.spawn(entities.CREEPER)   (ZOMBIE, SKELETON)

Event Handlers
~~~~~~~~~~~~~~

::

  def powerOn():
    # invoked when a redstone signal powers block
  def powerOff():
    # invoked when redstone signal stops powering block
  def tick():
    # invoked every server tick (20 times a second?)



Hand
----

::

    hand.forward()
    hand.forward(10)
    hand.backward(5)
    hand.reverse()
    hand.left()
    hand.right()
    hand.face(‘north’) (south, east, west)
    hand.move(x, y, z)

    hand.water()   # only if clear
    hand.lava()    # only if clear
    hand.clear()

    hand.put(blocks.COBBLESTONE)
    hand.line(5, blocks.STONE)
    hand.circle(5, blocks.STONE, False) // filled
    hand.ellipse(5, 10, blocks.STONE, True)
    hand.door(blocks.OAK_DOOR)
    hand.ladder(8, blocks.LADDER)



Wand
----

Invokes run() in the hand or block.


Building This Mod
=================

Three steps are needed to build this mod:

1. Get Forge (for minecraft 1.10) going, using the instructions here for
   IntelliJ setup:

   http://www.minecraftforge.net/forum/index.php/topic,21354.0.html

2. Add jython by including the following in the ``build.gradle``
   dependencies section:

       // https://mvnrepository.com/artifact/org.python/jython-standalone
       compile group: 'org.python', name: 'jython-standalone', version: '2.7.0'

3. Check this git repository out to replace the "src" folder of the Forge
   folder.

You should now be able to compile and run minecraft with this mod.


Distribution
------------

Update the version string in::

  build.gradle
  main/resources/mcmod.info

Then run::

  ./gradlew build

And upload the .jar file from ``build/libs/``.

TODO
====

*building*
- distribute the build.gradle changes required to build

*editing*
- selection-based copy/cut/paste
- scrolling rather than paging
- filename to tooltip / save as

*wand*
- bring up a REPL when activated against air

*blocks*
- pull from inventory, push out
- output: generates redstone signal
- wiring: for linking the above together
