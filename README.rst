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

*Redstone Power*
   If you define a powerOn() or powerOff() function these will be called
   when the block receives, or loses redstone power (eg from redstone
   wiring, or just a button placed on the block.)
*Time Passsing*
   If you define a tick() function this will be called about 20 times
   per second.


Recipes
-------

Wand
~~~~

Wands are used to create Python Code Books and interact
with Python Blocks and Python Hands.

.. image:: screenshot/wand-recipe.png

Python Block
~~~~~~~~~~~~

Python blocks sit in the world and can do stuff.

.. image:: screenshot/block-recipe.png

Hand
~~~~

Python hands can move around in the world and can do stuff.

.. image:: screenshot/hand-recipe.png

Python Code Book
~~~~~~~~~~~~~~~~

Craft a Python Wand with a Writeable Book. It's a superior
editable book that will also check your Python code for
syntax errors.

.. image:: screenshot/book-recipe.png


Functionality
-------------

The Hand and Block share a bunch of functionality. For example, to make a
message appear in the in-game chat, you would use::

    chat("hello, world!")

From this point on, I will refer to the block name, but you can also
use the "hand" name here too:

``world``
  The world that the block or hand belongs to.
``pos``
  The block-space position of the block or hand. Block space uses only
  integer (whole) numbers to locate things in the world using X
  (East/West), Y (Up/Down) and Z (North/South) coordinates. You can get a
  new pos by adding or moving the block's pos around, like::

    pos.up()
    pos.left()
    pos.add(1, 0, 4)   # East/X 1, Up/Y 0 and North/Z 4

``player``
  The player that loaded code into the block or hand.
``blocks``
  Holds all of the blocks in the game, for example::

    blocks.STONE
    blocks.COBBLESTONE
    blocks.BED
    blocks.LADDER
    blocks.TORCH

``items``
  Holds all of the items in the game, for example::

    items.TORCH
    items.IRON_SHOVEL
    items.WATER_BUCKET

``entities``
  This lists some of the entities in the game, allowing them to be spawned::

    entities.ZOMBIE
    entities.CREEPER
    entities.SKELETON

``chat("message")``
  Have the message appear in the in-game chat.

``water(pos)``
  Have a water source be created at the position, for example
  ``water(pos.up())``.

.. note:: Using water, lava or clear with the Python Block position will
          replace the block!

``lava(pos)``
  Have a water source be created at the position.

``clear(pos)``
  Clear the block at the position nominated.


Event Handlers
~~~~~~~~~~~~~~

Both Python Blocks and Python Hands may define a ``run()`` function that
will be invoked when the block or hand is activated (right-clicked) with
the Python Wand. For example, on a block::

  def run():
    block.firework()



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

For example::

    def powerOn():
      block.firework()

or::

    def powerOn():
      block.spawn(entities.ZOMBIE)




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

A more complete example which creates a little two-storey
tower with a door, bed and ladder from ground up to the roof::

    def run():
      for i in range(8):
        hand.ellipse(5, 5, blocks.STONE, i in (3, 7))
        if i in (0, 4): hand.put(blocks.TORCH)
        if i == 4:
          hand.left()
          hand.put(blocks.BED)
          hand.right()
        hand.up()
      hand.down(8)
      hand.backward(6)
      hand.put(blocks.OAK_DOOR)
      hand.forward(9)
      hand.ladder(8, blocks.LADDER)


Wand
----

Invokes run() in the hand or block, if that function is defined.


Building This Mod
=================

Three steps are needed to build this mod:

1. Get Forge (for minecraft 1.10) going, using the instructions for
   `IntelliJ setup`_:
2. Copy the ``build.gradle`` from the `Reference`_ section below.
3. Check this git repository out to replace the top-level "src" folder of the
   Forge setup you've created. Something like this in the folder created
   by the Forge setup::

    git clone git@github.com:r1chardj0n3s/pycode-minecraft.git src

   You should probably fork your own copy of the repository on
   github and clone that rather than clone my repository directly.

You should now be able to compile and run minecraft with this mod.

.. _`IntelliJ setup`: http://www.minecraftforge.net/forum/index.php/topic,21354.0.html


Distribution
------------

Update the version string in::

  build.gradle

Then run::

  ./gradlew build

And upload the .jar file from ``build/libs/``.

TODO
====

*editing*
 - selection-based copy/cut/paste
 - scrolling rather than paging
 - filename to tooltip / save as
 - add help button (describe key controls, mouse control)
 - blocks / items / entities listing somehow
*common code*
 - handle keyword arguments to provide colour, explicit facing or other
   blockstate customisation to put()
*wand*
 - bring up a REPL when activated against air?
 - REPL would want to have auto-complete
*blocks*
 - pull from inventory, push out
 - generate redstone power
 - wiring: for linking the above together? or is redstone enough?


Reference
=========

The ``build.gradle`` file I use::

    buildscript {
        repositories {
            jcenter()
            maven {
                name = "forge"
                url = "http://files.minecraftforge.net/maven"
            }
        }
        dependencies {
            classpath 'net.minecraftforge.gradle:ForgeGradle:2.2-SNAPSHOT'
        }
    }
    apply plugin: 'net.minecraftforge.gradle.forge'

    version = "1.1"
    group= "net.mechanicalcat.pycode" // http://maven.apache.org/guides/mini/guide-naming-conventions.html
    archivesBaseName = "pycode"
    sourceCompatibility = 8
    targetCompatibility = 8

    minecraft {
        version = "1.10.2-12.18.1.2011"
        runDir = "run"
        
        // the mappings can be changed at any time, and must be in the following format.
        // snapshot_YYYYMMDD   snapshot are built nightly.
        // stable_#            stables are built at the discretion of the MCP team.
        // Use non-default mappings at your own risk. they may not allways work.
        // simply re-run your setup task after changing the mappings to update your workspace.
        mappings = "snapshot_20160518"
        makeObfSourceJar = false // an Srg named sources jar is made by default. uncomment this to disable.
    }

    configurations {
        embed
        compile.extendsFrom(embed)
    }

    dependencies {
        // from https://mvnrepository.com/artifact/org.python/jython-standalone
        embed group: 'org.python', name: 'jython-standalone', version: '2.7.0'
    }

    jar {
        // exclude the exe installer stubs in jython - the curseforge folks don't like them!
        from configurations.embed.collect { it.isDirectory() ? it : zipTree(it).matching {exclude '**/*.exe'}  }
    }

    processResources {
        // this will ensure that this task is redone when the versions change.
        inputs.property "version", project.version
        inputs.property "mcversion", project.minecraft.version

        // replace stuff in mcmod.info, nothing else
        from(sourceSets.main.resources.srcDirs) {
            include 'mcmod.info'
                    
            // replace version and mcversion
            expand 'version':project.version, 'mcversion':project.minecraft.version
        }
            
        // copy everything else, thats not the mcmod.info
        from(sourceSets.main.resources.srcDirs) {
            exclude 'mcmod.info'
        }
    }

    idea { module { inheritOutputDirs = true } }

