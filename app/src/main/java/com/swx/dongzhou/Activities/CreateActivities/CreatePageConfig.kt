package com.swx.dongzhou.Activities.CreateActivities

import com.swx.dongzhou.R
import com.swx.dongzhou.Util.QRCodeType

enum class CreatePageMode {
    APP, FORM
}

enum class CreateFieldType {
    INPUT, MULTILINE, TEXT_COUNTER, DROPDOWN, SWITCH, TIME
}

data class CreateFieldConfig(
    val key: String,
    val hint: String = "",
    val label: String? = null,
    val iconRes: Int = 0,
    val type: CreateFieldType = CreateFieldType.INPUT,
    val required: Boolean = false,
    val options: List<String> = emptyList(),
    val defaultValue: String = "",
    val visibleOnStart: Boolean = true
)

data class CreatePageConfig(
    val type: QRCodeType,
    val title: String,
    val mode: CreatePageMode,
    val iconRes: Int,
    val tabs: List<String> = emptyList(),
    val tabHints: Map<String, String> = emptyMap(),
    val appHint: String = "",
    val showOpenTag: Boolean = false,
    val showCountryCode: Boolean = false,
    val countryCode: String = "+1",
    val description: String = "",
    val showQuickText: Boolean = false,
    val fields: List<CreateFieldConfig> = emptyList()
)

object CreatePageConfigs {
    const val EXTRA_CREATE_TYPE = "extra_create_type"
    const val EXTRA_PREFILL_TEXT = "extra_prefill_text"

    fun getConfig(type: QRCodeType): CreatePageConfig {
        return when (type) {
            QRCodeType.Website -> formConfig(
                type = type,
                title = "Website",
                iconRes = R.mipmap.ic_url,
                showQuickText = true,
                fields = listOf(
                    CreateFieldConfig(
                        key = "url",
                        hint = "Enter URL",
                        iconRes = R.mipmap.ic_url_unselected,
                        required = true
                    )
                )
            )

            QRCodeType.WIFI -> formConfig(
                type = type,
                title = "WIFI",
                iconRes = R.mipmap.ic_wifi,
                fields = listOf(
                    CreateFieldConfig(
                        key = "wifiName",
                        hint = "Network name",
                        iconRes = R.mipmap.ic_wifi_unselected,
                        required = true
                    ),
                    CreateFieldConfig(
                        key = "security",
                        hint = "Security type",
                        iconRes = R.mipmap.ic_create_type,
                        type = CreateFieldType.DROPDOWN,
                        options = listOf("WPA/WPA2", "WEP", "None"),
                        defaultValue = "WPA/WPA2"
                    ),
                    CreateFieldConfig(
                        key = "password",
                        hint = "Password",
                        iconRes = R.mipmap.ic_create_password
                    )
                )
            )

            QRCodeType.Text -> formConfig(
                type = type,
                title = "Text",
                iconRes = R.mipmap.ic_text,
                fields = listOf(
                    CreateFieldConfig(
                        key = "text",
                        hint = "Enter text",
                        type = CreateFieldType.TEXT_COUNTER,
                        required = true
                    )
                )
            )

            QRCodeType.Contact -> formConfig(
                type = type,
                title = "Contact",
                iconRes = R.mipmap.ic_contact,
                fields = listOf(
                    CreateFieldConfig("name", "Name", iconRes = R.mipmap.ic_create_name, required = true),
                    CreateFieldConfig("phone1", "Phone number", iconRes = R.mipmap.ic_create_phone, required = true),
                    CreateFieldConfig("phone2", "Second phone number", iconRes = R.mipmap.ic_create_phone)
                )
            )

            QRCodeType.Tel -> formConfig(
                type = type,
                title = "Tel",
                iconRes = R.mipmap.ic_tel,
                fields = listOf(
                    CreateFieldConfig(
                        key = "phone",
                        hint = "Phone number",
                        iconRes = R.mipmap.ic_create_phone,
                        required = true
                    )
                )
            )

            QRCodeType.Email -> formConfig(
                type = type,
                title = "E-mail",
                iconRes = R.mipmap.ic_email,
                fields = listOf(
                    CreateFieldConfig("email", "E-mail", iconRes = R.mipmap.ic_email_unselected, required = true),
                    CreateFieldConfig("subject", "Subject", label = "Subject", visibleOnStart = false),
                    CreateFieldConfig(
                        key = "content",
                        hint = "Content",
                        label = "Content",
                        type = CreateFieldType.MULTILINE,
                        visibleOnStart = false
                    )
                )
            )

            QRCodeType.SMS -> formConfig(
                type = type,
                title = "SMS",
                iconRes = R.mipmap.ic_sms,
                fields = listOf(
                    CreateFieldConfig("phone", "Phone number", iconRes = R.mipmap.ic_create_phone, required = true),
                    CreateFieldConfig(
                        key = "message",
                        hint = "Message content",
                        label = "Message",
                        type = CreateFieldType.MULTILINE
                    )
                )
            )

            QRCodeType.Calendar -> formConfig(
                type = type,
                title = "Calendar",
                iconRes = R.mipmap.ic_calendar,
                fields = listOf(
                    CreateFieldConfig("title", "Title", label = "Title", required = true),
                    CreateFieldConfig("location", "Location", label = "Location", visibleOnStart = false),
                    CreateFieldConfig(
                        key = "allDay",
                        label = "All day",
                        type = CreateFieldType.SWITCH,
                        defaultValue = "false"
                    ),
                    CreateFieldConfig("startTime", label = "Start", type = CreateFieldType.TIME, defaultValue = "Jan 8 9:30"),
                    CreateFieldConfig("endTime", label = "End", type = CreateFieldType.TIME, defaultValue = "11:30"),
                    CreateFieldConfig(
                        key = "description",
                        hint = "Description",
                        label = "Description",
                        type = CreateFieldType.MULTILINE
                    )
                )
            )

            QRCodeType.MyCard -> formConfig(
                type = type,
                title = "My Card",
                iconRes = R.mipmap.ic_mecard,
                fields = listOf(
                    CreateFieldConfig("name", "Name", iconRes = R.mipmap.ic_create_name, required = true),
                    CreateFieldConfig("phone", "Phone number", iconRes = R.mipmap.ic_create_phone),
                    CreateFieldConfig("email", "E-mail", iconRes = R.mipmap.ic_email_unselected),
                    CreateFieldConfig("address", "Address", iconRes = R.mipmap.ic_create_address),
                    CreateFieldConfig("birthday", label = "Birthday", type = CreateFieldType.TIME, defaultValue = "Jan 8"),
                    CreateFieldConfig("org", "Organization", iconRes = R.mipmap.ic_create_org),
                    CreateFieldConfig("note", "Note", label = "Note", type = CreateFieldType.MULTILINE)
                )
            )

            QRCodeType.FaceBook -> appConfig(
                type = type,
                title = "FaceBook",
                iconRes = R.mipmap.ic_facebook,
                tabs = listOf("Facebook ID", "URL"),
                tabHints = mapOf("Facebook ID" to "Enter Facebook ID", "URL" to "Enter Facebook URL")
            )

            QRCodeType.Instagram -> appConfig(
                type = type,
                title = "Instagram",
                iconRes = R.mipmap.ic_ins,
                tabs = listOf("Username", "URL"),
                tabHints = mapOf("Username" to "Enter Instagram username", "URL" to "Enter Instagram URL")
            )

            QRCodeType.WhatsApp -> appConfig(
                type = type,
                title = "WhatsApp",
                iconRes = R.mipmap.ic_whatsapp,
                appHint = "Phone number",
                showCountryCode = true,
                showOpenTag = false
            )

            QRCodeType.Youtube -> appConfig(
                type = type,
                title = "YouTube",
                iconRes = R.mipmap.ic_youtobe,
                tabs = listOf("URL", "Video ID", "Channel ID"),
                tabHints = mapOf(
                    "URL" to "Enter YouTube URL",
                    "Video ID" to "Enter YouTube Video ID",
                    "Channel ID" to "Enter YouTube Channel ID"
                )
            )

            QRCodeType.Twitter -> appConfig(
                type = type,
                title = "Twitter",
                iconRes = R.mipmap.ic_twitter,
                tabs = listOf("Username", "URL"),
                tabHints = mapOf("Username" to "Enter Twitter username", "URL" to "Enter Twitter URL")
            )

            QRCodeType.Spotify -> appConfig(
                type = type,
                title = "Spotify",
                iconRes = R.mipmap.ic_spotify,
                appHint = "Artist name"
            )

            QRCodeType.Paypal -> appConfig(
                type = type,
                title = "PayPal",
                iconRes = R.mipmap.ic_paypal,
                tabs = listOf("Me Link", "Me Username"),
                tabHints = mapOf("Me Link" to "Enter PayPal me link", "Me Username" to "Enter PayPal username"),
                showOpenTag = false,
                showQuickText = true
            )

            QRCodeType.Viber -> appConfig(
                type = type,
                title = "Viber",
                iconRes = R.mipmap.ic_viber,
                appHint = "Phone number",
                showCountryCode = true,
                countryCode = "+971",
                description = "Others can contact you directly on Viber by scanning the QR\ncode you create",
                showOpenTag = false
            )
        }
    }

    fun createContent(
        type: QRCodeType,
        selectedTab: String,
        values: Map<String, String>,
        switches: Map<String, Boolean>
    ): String {
        return when (type) {
            QRCodeType.Website -> values.value("url")
            QRCodeType.WIFI -> "WIFI:T:${values.value("security")};S:${values.value("wifiName")};P:${values.value("password")};;"
            QRCodeType.Text -> values.value("text")
            QRCodeType.Contact -> buildLines(
                "BEGIN:VCARD",
                "FN:${values.value("name")}",
                "TEL:${values.value("phone1")}",
                "TEL:${values.value("phone2")}",
                "END:VCARD"
            )
            QRCodeType.Tel -> "tel:${values.value("phone")}"
            QRCodeType.Email -> buildLines(
                "mailto:${values.value("email")}",
                "subject=${values.value("subject")}",
                "body=${values.value("content")}"
            )
            QRCodeType.SMS -> "SMSTO:${values.value("phone")}:${values.value("message")}"
            QRCodeType.Calendar -> buildLines(
                "BEGIN:VEVENT",
                "SUMMARY:${values.value("title")}",
                "LOCATION:${values.value("location")}",
                "DTSTART:${values.value("startTime")}",
                "DTEND:${values.value("endTime")}",
                "DESCRIPTION:${values.value("description")}",
                "ALL_DAY:${switches["allDay"] == true}",
                "END:VEVENT"
            )
            QRCodeType.MyCard -> buildLines(
                "MECARD:N:${values.value("name")};",
                "TEL:${values.value("phone")};",
                "EMAIL:${values.value("email")};",
                "ADR:${values.value("address")};",
                "BDAY:${values.value("birthday")};",
                "ORG:${values.value("org")};",
                "NOTE:${values.value("note")};;"
            )
            QRCodeType.FaceBook -> appContent("facebook", selectedTab, values.value("appInput"))
            QRCodeType.Instagram -> appContent("instagram", selectedTab, values.value("appInput"))
            QRCodeType.WhatsApp -> "https://wa.me/${values.value("appInput")}"
            QRCodeType.Youtube -> appContent("youtube", selectedTab, values.value("appInput"))
            QRCodeType.Twitter -> appContent("twitter", selectedTab, values.value("appInput"))
            QRCodeType.Spotify -> "spotify:${values.value("appInput")}"
            QRCodeType.Paypal -> "https://paypal.me/${values.value("appInput")}"
            QRCodeType.Viber -> "viber://chat?number=${values.value("appInput")}"
        }
    }

    private fun formConfig(
        type: QRCodeType,
        title: String,
        iconRes: Int,
        fields: List<CreateFieldConfig>,
        showQuickText: Boolean = false
    ): CreatePageConfig {
        return CreatePageConfig(
            type = type,
            title = title,
            mode = CreatePageMode.FORM,
            iconRes = iconRes,
            showQuickText = showQuickText,
            fields = fields
        )
    }

    private fun appConfig(
        type: QRCodeType,
        title: String,
        iconRes: Int,
        tabs: List<String> = emptyList(),
        tabHints: Map<String, String> = emptyMap(),
        appHint: String = "",
        showOpenTag: Boolean = true,
        showCountryCode: Boolean = false,
        countryCode: String = "+1",
        description: String = "",
        showQuickText: Boolean = false
    ): CreatePageConfig {
        return CreatePageConfig(
            type = type,
            title = title,
            mode = CreatePageMode.APP,
            iconRes = iconRes,
            tabs = tabs,
            tabHints = tabHints,
            appHint = appHint,
            showOpenTag = showOpenTag,
            showCountryCode = showCountryCode,
            countryCode = countryCode,
            description = description,
            showQuickText = showQuickText
        )
    }

    private fun appContent(platform: String, selectedTab: String, input: String): String {
        return if (selectedTab == "URL" || input.startsWith("http")) {
            input
        } else {
            "$platform:$input"
        }
    }

    private fun buildLines(vararg lines: String): String {
        return lines.filter { line ->
            val value = line.substringAfter(":", missingDelimiterValue = line)
            value.isNotBlank()
        }.joinToString(separator = "\n")
    }

    private fun Map<String, String>.value(key: String): String {
        return this[key].orEmpty().trim()
    }
}
