package com.healthjournal.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.core.content.ContextCompat
import com.healthjournal.R
import com.google.android.material.textfield.TextInputEditText

class CustomEditText : TextInputEditText {

    private var errorBackground: Drawable? = null
    private var defaultBackground: Drawable? = null
    private var isError: Boolean = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = if (isError) {
            errorBackground
        } else {
            defaultBackground
        }
    }

    private fun init() {
        errorBackground = ContextCompat.getDrawable(context, R.drawable.bg_edt_error)
        defaultBackground = ContextCompat.getDrawable(context, R.drawable.bg_edt_default)

        if (inputType == InputType.TYPE_CLASS_NUMBER) {
            filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
                source.filter { it.isDigit() }
            })
        }

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val input = p0.toString()
                if (input.isEmpty()) {
                    error = context.getString(R.string.empty_field)
                    isError = true
                } else {
                    error = null
                    isError = false
                    validateInput(input)
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun validateInput(input: String) {
        when (inputType) {
            EMAIL -> {
                if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                    error = context.getString(R.string.email_validation)
                    isError = true
                } else {
                    isError = false
                }
            }
            PASSWORD -> {
                if (input.length < 6) {
                    error = context.getString(R.string.password_length)
                    isError = true
                } else {
                    isError = false
                }
            }
            InputType.TYPE_CLASS_NUMBER -> {
                if (!input.matches("\\d+".toRegex())) {
                    error = context.getString(R.string.empty_field)
                    isError = true
                } else {
                    isError = false
                }
            }
        }
    }

    companion object {
        const val EMAIL = 0x00000021
        const val PASSWORD = 0x00000081
    }
}
