package com.example.a7minworkoutapp

class ExerciseModel(
    private var id: Int,
    private var name: String,
    private var image: Int,
    private var isCompleted: Boolean,
    private var isSelected: Boolean
) {
    fun getId():Int{
        return id
    }
    
    fun setId(id_:Int){
        this.id=id_
    }

    fun getName():String{
        return name
    }

    fun setName(name_:String){
        this.name=name_
    }

    fun getImage():Int{
        return image
    }

    fun setImage(image_:Int){
        this.image=image_
    }

    fun getIsCompleted():Boolean{
        return isCompleted
    }

    fun setIsCompleted(isCompleted_:Boolean){
        this.isCompleted=isCompleted_
    }

    fun getIsSelected():Boolean{
        return isSelected
    }

    fun setIsSelected(isSelected_:Boolean){
        this.isSelected=isSelected_
    }

}