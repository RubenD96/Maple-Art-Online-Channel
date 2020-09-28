package client

import field.obj.life.AbstractFieldLife

abstract class SimpleCharacter : AbstractFieldLife() {

    abstract var face: Int
    abstract var hair: Int
    abstract var gender: Int
    abstract var skinColor: Int
    abstract var fieldId: Int
}