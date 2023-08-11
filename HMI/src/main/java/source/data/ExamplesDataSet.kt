package org.rajawali3d.examples.data

import org.rajawali3d.examples.R
import org.rajawali3d.examples.DreamView.general.ColoredLinesFragment
import java.util.*

class ExamplesDataSet private constructor() {
    val categories: List<Category> = createCategories()

    companion object {
        @JvmStatic
        @get:Synchronized
        @Volatile
        var instance: ExamplesDataSet? = null
            get() {
                if (field == null) {
                    synchronized(ExamplesDataSet::class.java) {
                        if (field == null) {
                            field = ExamplesDataSet()
                        }
                    }
                }
                return field
            }
            private set

        private fun createCategories(): List<Category> {
            val categories: MutableList<Category> = LinkedList()
            categories.add(
                Category(
                    R.string.category_general, arrayOf(
                        Example(R.string.example_general_colored_lines, ColoredLinesFragment::class.java),
                    )
                )
            )

            return categories
        }
    }
}