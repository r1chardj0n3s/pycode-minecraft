===================
Python in Minecraft
===================

I wanted to code Python stuff in Minecraft, so added a Python Block and
movable Python Hand which can be programmed with Python. The code may be
edited in-game using standard writeable books, which suck, so I also
created an extended book which provides more space, cursor and mouse
control, syntax checking and some other features.

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

**Python Wands** are used to create Python Code Books and interact
with Python Blocks and Python Hands:

.. image:: screenshot/wand-recipe.png

**Python Blocks** sit in the world and can do stuff:

.. image:: screenshot/block-recipe.png

**Python Hands** can move around in the world and can do stuff.

.. image:: screenshot/hand-recipe.png

**Python Code Book** is a superior editable book that will also check
your Python code for syntax errors:

.. image:: screenshot/book-recipe.png


Functionality
-------------

The Hand and Block share a bunch of functionality. For example, to make a
message appear in the in-game chat, you would use::

    chat("hello, world!")

From this point on, I will refer to the block name, but you can also
use the "hand" name here too:

``pos``
  The block-space position of the block or hand. Block space uses only
  integer (whole) numbers to locate things in the world using X
  (East/West), Y (Up/Down) and Z (North/South) coordinates. You can get a
  new pos by adding or moving the block's pos around, like::

    pos.up()
    pos.east()
    pos.add(1, 0, 4)   # East/X 1, Up/Y 0 and North/Z 4
``chat("message")``
  Have the message appear in the in-game chat.
``water(pos)``
  Have a water source be created at the position, for example
  ``water(pos.up())``. This will only work if the target position is clear.
``lava(pos)``
  Have a water source be created at the position. This will only work if
  the target position is clear.
``clear(pos)``
  Clear the block at the position nominated.

  Note: using clear with the Python Block position will remove the block!


Event Handlers
~~~~~~~~~~~~~~

Both Python Blocks and Python Hands may define a ``run()`` function that
will be invoked when the block or hand is activated (right-clicked) with
the Python Wand. For example, on a block::

  def run():
    block.firework()


Block
-----

Doc TBD:: 

    block.powered        # (boolean)

    block.firework()

    block.spawn('creeper')      # ('zombie', 'skeleton')

Event Handlers
~~~~~~~~~~~~~~

Doc TBD::

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
      block.spawn('zombie')


Hand
----

Doc TBD::

    hand.forward()
    hand.forward(10)
    hand.backward(5)
    hand.reverse()
    hand.left()
    hand.right()
    hand.face(‘north’)      #  ('south', 'east', 'west')
    hand.move(x, y, z)

    hand.storePos()
    hand.recallPos()   # moves hand back to the stored pos/facing

    hand.water()   # only if clear
    hand.lava()    # only if clear
    hand.clear()

    hand.put('cobblestone')
    hand.line(5, 'stone')
    hand.circle(5, 'stone')             # unfilled
    hand.disk(5, 'stone')               # filled
    hand.ellipse(5, 10, 'stone', True)  # True=filled

    # if a block has orientation, it is taken from the hand's direction
    hand.put(8, 'torch')

    # place a bunch of the block in a vertical line
    hand.put(8, 'ladder')

    # beds and door special double blocks are handled
    hand.put('wooden_door')
    hand.put('bed')

A more complete example which creates a little two-storey
tower with a door, bed and ladder from ground up to the roof::

    def run():
      hand.down()
      hand.disk(5, 'cobblestone')
      for i in range(8):
        hand.up()
        if i in (3, 7):
          hand.disk(5, 'planks')
        hand.circle(5, 'stone')
        if i in (0, 4):
          hand.put('torch')
          hand.reverse()
          hand.put('bed')
      hand.down(7); hand.backward(6)
      for i in range(3): hand.clear(); hand.up()
      hand.down(); hand.forward()
      hand.put('cobblestone')
      hand.down(2)
      hand.put('wooden_door')
      hand.forward(8)
      hand.ladder(8, 'ladder')


Wand
----

Invokes run() in the hand or block, if that function is defined.


CHANGELOG
=========

**1.3**
 - Replaced blocks, items and entities with string inputs.
**1.2**
 - Moved chat/lava/water/clear to be top-level functions
 - Lots of documentation
**1.1**
 - Packaging fixes (removed the .exe files from the jython redist)
**1.0**
 -  Initial release! Had the Python Code Book, Hand, Block and Wand.


Contributing
============

This mod is open source and contributors are welcomed! The project
is hosted on `github`_. If you need help with git, please let me
know!

.. _`github`: https://github.com/r1chardj0n3s/pycode-minecraft


Building This Mod
-----------------

Three steps are needed to build this mod:

1. Get Forge (for minecraft 1.10) going, using the instructions for
   `IntelliJ setup`_.
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
  net.mechanicalcat.pycode.Reference.VERSION

Then run::

  ./gradlew build

And upload the .jar file from ``build/libs/``.


TODO
----

This is not an exhaustive list, and should probably be put into github issues.

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
 - texture map replacement
*blocks and hands*
 - model replacement (OBJ, ?)
 - inventory


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

