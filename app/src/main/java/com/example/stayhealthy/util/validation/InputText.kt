package com.example.stayhealthy.util.validation

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData

open class InputText(private val inputType: InputType? = null, errorDebounce: Long = 200L) : Input<String> {

    override val input: MutableLiveData<String> get() = liveData

    private var liveData = MutableLiveData<String>()

    private val debounceHandler = Handler()
    private val setErrorRunnable = Runnable {
        errorLiveData.value = validate(value, inputType)
    }

    override var value: String?
        get() = liveData.value
        set(value) {
            value?.let { liveData.value = it }
        }

    override val error: LiveData<Int?>
        get() = errorLiveData

    private val errorLiveData: MutableLiveData<Int?> = MediatorLiveData<Int?>().apply {
        addSource(input) {
            debounceHandler.removeCallbacks(setErrorRunnable)
            debounceHandler.postDelayed(setErrorRunnable, errorDebounce)
        }
    }

    //this is needed because mapped LiveData's getValue() always returns null
    override val errorValue: Int?
        get() = validate(value, inputType)

    override fun validate(): Boolean {
        //trigger setting errors when attempting form action
        if (value == null && inputType != null) {
            liveData.postValue("")
        }

        return inputType?.let { errorValue == null && value != null } ?: true
    }

    fun resetInput() {
        liveData = MutableLiveData()
    }
}