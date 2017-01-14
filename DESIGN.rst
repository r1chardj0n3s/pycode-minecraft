=======================
PyCode-Minecraft Design
=======================


This Minecraft mod comprises three main components:

Minecraft Forge
  This provides deobfuscated minecraft code and a framework for plugging the mod into
  Minecraft. It is managed through gradle, which is configured in ``build.gradle``.
  The mod largely follows common community best practise pattersn for organising
  a Forge-based mod package.

Jython
  The other 3rd party component is the Java implementation of Python called Jython.
  This is included as a library, which ``build.gradle`` has configured as a dependency.

net.mechanicalcat.pycode package
  The mod code is all in this package, which ties together Minecraft and Jython. All
  packages referred to below are rooted at that net.mechanicalcat.pycode package.


Python Code
===========

At the core of the execution of Python code in Minecraft are the PythonEngine and
script.PythonCode classes. These pull in Jython using the standard Java Script Engine.
I had to include some glue in the script.jython package - the classes therein are
the only ones in the net.mechanicalcat.pycode package which were not written by
me (though I have modified them) and they are copyright Sun Microsystems. There didn't
seem to be an easier method of including that 3rd party code though, apart from
vendoring it.

The PythonEngine class sets up the singleton ScriptEngine which is used to execute
all the Python code. The PythonCode class encapsulates the program code for a single
source in the game - one writeable book's worth. Each program code has its own
context and variable bindings, just like a Python program would have. The PythonCode
class is then also responsible for compiling and executing that program code, and
managing interactions with the global variable bindings. Other bits of the mod (for
example the Python Hand (in entities.HandEntity) have instances of PythonCode and are
responsible for setting the context of the code and any other variable bindings specific
to themselves (for example the HandEntity sets the "hand" variable).


Code Interface (API)
--------------------

Most of the functions and methods that players can invoke in their Python code in-game
are defined in the script package in classes like BlockMethods (ie. methods specific
to the block object), HandMethods and some functions and variables on the PythonCode
class itself (which are inserted into the globals namespace using a bit of a hack
since Java doesn't have first-class function objects).

Some of the code is split out where it makes sense, like the roof and shape generation
code, which is quite large by itself, and can be shared between the hand and block
methods classes.

The intention with the API provided to players is to simplify the rather complex
Minecraft code for the player, allowing short code to achieve amazing things.


Obfuscation Issues
==================

Since Minecraft runs obfuscated, we can't just include a global variable in the Python
context called "blocks" which pulls in net.minecraft.init.Blocks so we can access
all the lovely names on that class like Blocks.DIRT and so on - because "DIRT" is
obfuscated into some random non-human-readle gibberish. To work around this, the
mod has a couple of approaches:

1. For identifying types of things in the game like block types, potions, entities
   and so on, we use plain Python strings and perform a lookup using that string
   in our code interface classes.
2. To allow access attributes of, and invoking methods on objects like BlockPos, or
   EntityPlayer, we wrap those objects in a "My" class like MyBlockPos or
   MyEntityPlayer, exposing functionality that seems like a good idea. A big benefit
   of this is being able to add sensible new APIs for player convenience, like
   MyEntity.potion() - which is perhaps surprisingly complex under the hood.

Though it may be obvious, it's important to point out that the PyCode mod itself
is not obfuscated, else it would not work.


Client/Server Issues
====================

The code is compiled and run on both the client and the server - it is the
responsibility of individual API endpoints to determine whether they should be
executed on the client or the server. Most methods modify world state, and
should be run on the server. If, however, a method generates a GUI effect
(apart from particles, and possibly other things that need to be server-side) then
it should run on the client. A good example of this is the right-click handler fo
the Python Wand, which must determine what the player is looking at; the objectMouseOver
attribute that Minecraft provides only exists on the client, but we must do the
processing of the action on the server, so we send the event over internal Minecraft
networking (see net.InvokeWandMessage and its use).

Also, sometimes the world attribute is null. No idea why. That's why you'll see
this almost everywhere::

          if (this.world == null || this.world.isRemote) return;
