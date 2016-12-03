===================
Python in Minecraft
===================

I wanted to code Python stuff in Minecraft, so I smooshed jython
in and added some extra stuff.


What you can do
===============

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



TODO
====

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
