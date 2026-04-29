# Resource based Memory Management

Basically we turn memory management into a resource management

We make resources for the objects that we instantiate from classes

Each resource will have an owner, when that owner gets destroyed, their resources will be destroyed as well.

Resources have different operations:
- creating a resource from a class (calling a constructor)
- destroying a resource (done automatically when owner gets destroyed)
- tying resources to other resources or function scopes, making their lifecycle dependent on other lifecycles
    - with using and take
- sharing a resource by reference

A resource can be in four kind of state:

- Resource: we have an object that we own in this context
- Takable resource: we promised that we manage this resources ownership, but it's not done yet (this state is not a valid 'final' state)
- Reference: a shared resource, which ownership cannot be modified in this context
- View: immutable reference


