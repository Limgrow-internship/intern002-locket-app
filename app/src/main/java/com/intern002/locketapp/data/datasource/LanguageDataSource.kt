package com.intern002.locketapp.data.datasource

import com.intern002.locketapp.data.remote.model.Language

object LanguageDataSource {

    fun getLanguages(): List<Language> {
        return listOf(
            Language("en", "English", "https://flagcdn.com/w320/gb.png"),
            Language("vi", "Tiếng Việt", "https://flagcdn.com/w320/vn.png"),
            Language("ja", "日本語", "https://flagcdn.com/w320/jp.png"),
            Language("ko", "한국어", "https://flagcdn.com/w320/kr.png"),
            Language("th", "ไทย", "https://flagcdn.com/w320/th.png"),
            Language("zh", "中文", "https://flagcdn.com/w320/cn.png"),
            Language("es", "Español", "https://flagcdn.com/w320/es.png"),
            Language("fr", "Français", "https://flagcdn.com/w320/fr.png"),
        ).sortedBy { it.name }
    }
}