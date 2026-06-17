package com.swx.dongzhou.Activities.CreateActivities

import android.view.View
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.swx.dongzhou.BaseActivity
import com.swx.dongzhou.databinding.ActivityCalendarCreateBinding

class CalendarCreateActivity : BaseActivity<ActivityCalendarCreateBinding>(
    ActivityCalendarCreateBinding::inflate
) {

    private var locationExpanded = false
    private var isAllDay = false

    override fun initView() {
        binding.tvTitle.text = "Calendar"

        binding.layoutLocationArea.visibility = View.GONE
        binding.tvConfirm.visibility = View.GONE
        binding.btnCreate.visibility = View.VISIBLE

        binding.tvStartTime.text = "Jan 8 9:30"
        binding.tvEndTime.text = "11:30"
    }

    override fun initAction() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.tvConfirm.setOnClickListener {
            handleCreate()
        }

        binding.btnCreate.setOnClickListener {
            handleCreate()
        }

        binding.etCalendarTitle.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                expandLocationArea()
            }
        }

        binding.layoutTitleInput.setOnClickListener {
            expandLocationArea()
            binding.etCalendarTitle.requestFocus()
        }

        binding.switchAllDay.setOnCheckedChangeListener { _, checked ->
            isAllDay = checked
            updateAllDayState(checked)
        }

        binding.layoutStartTime.setOnClickListener {
            showStartTimePicker()
        }

        binding.layoutEndTime.setOnClickListener {
            showEndTimePicker()
        }

        initKeyboardState()
    }

    private fun expandLocationArea() {
        if (locationExpanded) return

        locationExpanded = true
        binding.layoutLocationArea.visibility = View.VISIBLE
    }

    private fun updateAllDayState(allDay: Boolean) {
        if (allDay) {
            binding.tvStartTime.text = "Jan 8"
            binding.tvEndTime.text = "Jan 8"
        } else {
            binding.tvStartTime.text = "Jan 8 9:30"
            binding.tvEndTime.text = "11:30"
        }
    }

    private fun initKeyboardState() {
        val root = binding.root

        val initialLeft = root.paddingLeft
        val initialTop = root.paddingTop
        val initialRight = root.paddingRight
        val initialBottom = root.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())

            binding.tvConfirm.visibility = if (imeVisible) View.VISIBLE else View.GONE
            binding.btnCreate.visibility = if (imeVisible) View.GONE else View.VISIBLE

            val bottomPadding = if (imeVisible) {
                imeInsets.bottom
            } else {
                systemBarsInsets.bottom
            }

            view.updatePadding(
                left = initialLeft + systemBarsInsets.left,
                top = initialTop + systemBarsInsets.top,
                right = initialRight + systemBarsInsets.right,
                bottom = initialBottom + bottomPadding
            )

            insets
        }
    }

    private fun showStartTimePicker() {
        Toast.makeText(this, "Select start time", Toast.LENGTH_SHORT).show()

        // TODO: 后续可以在这里接入 MaterialDatePicker / MaterialTimePicker
        // 示例：
        // 选择完成后更新：
        // binding.tvStartTime.text = "Jan 8 9:30"
    }

    private fun showEndTimePicker() {
        Toast.makeText(this, "Select end time", Toast.LENGTH_SHORT).show()

        // TODO: 后续可以在这里接入 MaterialDatePicker / MaterialTimePicker
        // 示例：
        // 选择完成后更新：
        // binding.tvEndTime.text = "11:30"
    }

    private fun handleCreate() {
        val title = binding.etCalendarTitle.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val startTime = binding.tvStartTime.text.toString().trim()
        val endTime = binding.tvEndTime.text.toString().trim()

        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter title", Toast.LENGTH_SHORT).show()
            return
        }

        createCalendarQr(
            title = title,
            location = location,
            description = description,
            startTime = startTime,
            endTime = endTime,
            allDay = isAllDay
        )
    }

    private fun createCalendarQr(
        title: String,
        location: String,
        description: String,
        startTime: String,
        endTime: String,
        allDay: Boolean
    ) {
        val content = buildString {
            appendLine("BEGIN:VEVENT")
            appendLine("SUMMARY:$title")

            if (location.isNotEmpty()) {
                appendLine("LOCATION:$location")
            }

            if (description.isNotEmpty()) {
                appendLine("DESCRIPTION:$description")
            }

            appendLine("START:$startTime")
            appendLine("END:$endTime")
            appendLine("ALL_DAY:$allDay")
            appendLine("END:VEVENT")
        }

        // TODO: 在这里接入你的二维码生成结果页
        // val intent = Intent(this, QrResultActivity::class.java)
        // intent.putExtra("content", content)
        // startActivity(intent)

        Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
    }
}