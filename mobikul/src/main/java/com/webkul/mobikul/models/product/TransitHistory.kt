import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.webkul.mobikul.models.product.Transit
import java.util.ArrayList

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class TransitHistoryModel() : Parcelable {
    @JsonProperty("Transit")
    var Transit: ArrayList<Transit>? = ArrayList()

    constructor(parcel: Parcel) : this() {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TransitHistoryModel> {
        override fun createFromParcel(parcel: Parcel): TransitHistoryModel {
            return TransitHistoryModel(parcel)
        }

        override fun newArray(size: Int): Array<TransitHistoryModel?> {
            return arrayOfNulls(size)
        }
    }


}