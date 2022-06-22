package com.example.a7minworkoutapp

object Constants {

    fun defaultExerciseList(): ArrayList<ExerciseModel>{
        val exerciseList = ArrayList<ExerciseModel>()

        val jumpingJacks = ExerciseModel(
            1,
            "Jumping Jacks",
            R.drawable._01_jumping_jacks,
            false,
            false
        )
        exerciseList.add(jumpingJacks)

        val wallSits = ExerciseModel(
            2,
            "Wall Sits",
            R.drawable._02_wall_sit_lower_body,
            false,
            false
        )
        exerciseList.add(wallSits)

        val pushUps = ExerciseModel(
            3,
            "Push Ups",
            R.drawable._03_pushup_upper_body,
            false,
            false
        )
        exerciseList.add(pushUps)

        val abCrunch = ExerciseModel(
            4,
            "Ab Crunch",
            R.drawable._04_abdominal_crunch_core,
            false,
            false
        )
        exerciseList.add(abCrunch)

        val stepUp = ExerciseModel(
            5,
            "Step Up",
            R.drawable._05_stepup_onto_chair,
            false,
            false
        )
        exerciseList.add(stepUp)

        val squats = ExerciseModel(
            6,
            "Squats",
            R.drawable._06_squat_lower_body,
            false,
            false
        )
        exerciseList.add(squats)

        val tricepsDip = ExerciseModel(
            7,
            "Triceps Dip",
            R.drawable._07_triceps_dip,
            false,
            false
        )
        exerciseList.add(tricepsDip)

        val plank = ExerciseModel(
            8,
            "Plank",
            R.drawable._08_plank_core,
            false,
            false
        )
        exerciseList.add(plank)

        val highKnees = ExerciseModel(
            9,
            "High Knees",
            R.drawable._09_high_knees_lower_body,
            false,
            false
        )
        exerciseList.add(highKnees)

        val lunges = ExerciseModel(
            10,
            "Lunges",
            R.drawable._10_lunge_lower_body,
            false,
            false
        )
        exerciseList.add(lunges)

        val pushUpAndRotation = ExerciseModel(
            11,
            "Push Up and Rotation",
            R.drawable._11_pushup_and_rotation,
            false,
            false
        )
        exerciseList.add(pushUpAndRotation)

        val sidePlank = ExerciseModel(
            12,
            "Side Plank",
            R.drawable._12_side_plank_core,
            false,
            false
        )
        exerciseList.add(sidePlank)

        return exerciseList
    }
}