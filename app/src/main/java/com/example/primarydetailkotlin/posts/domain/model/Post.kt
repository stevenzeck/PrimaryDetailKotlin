package com.example.primarydetailkotlin.posts.domain.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.primarydetailkotlin.posts.domain.model.Post.Companion.TABLE_NAME
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data class representing a Post.
 *
 * This class serves multiple purposes:
 * 1. It acts as the entity for the Room database table.
 * 2. It is a serializable object for network parsing (using Kotlin Serialization).
 * 3. It is Parcelable for passing between Android components.
 *
 * @property id The unique identifier for the post. It is the primary key in the database.
 * @property userId The ID of the user who created the post.
 * @property title The title of the post.
 * @property body The content/body of the post.
 * @property read A flag indicating whether the post has been marked as read. Defaults to false.
 */
@Parcelize
@Serializable
@Entity(tableName = TABLE_NAME)
data class Post(
    @PrimaryKey
    @ColumnInfo(name = COLUMN_ID)
    @SerialName("id")
    val id: Long,

    @ColumnInfo(name = COLUMN_USER_ID)
    @SerialName("userId")
    val userId: Int,

    @ColumnInfo(name = COLUMN_TITLE)
    @SerialName("title")
    val title: String,

    @ColumnInfo(name = COLUMN_BODY)
    @SerialName("body")
    val body: String,

    @ColumnInfo(name = COLUMN_READ)
    val read: Boolean = false
) : Parcelable {

    companion object {

        /** The name of the table in the database. */
        const val TABLE_NAME = "post"

        /** Column name for the post ID. */
        const val COLUMN_ID = "id"

        /** Column name for the user ID. */
        const val COLUMN_USER_ID = "userId"

        /** Column name for the post title. */
        const val COLUMN_TITLE = "title"

        /** Column name for the post body. */
        const val COLUMN_BODY = "body"

        /** Column name for the read status. */
        const val COLUMN_READ = "read"
    }
}
