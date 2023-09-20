package com.webkul.mobikul.models


import androidx.databinding.BaseObservable
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty


/**
 * Webkul Software.
 *
 * Kotlin
 *
 * @author Webkul <support@webkul.com>
 * @category Webkul
 * @package com.webkul.mobikul
 * @copyright 2010-2018 Webkul Software Private Limited (https://webkul.com)
 * @license https://store.webkul.com/license.html ASL Licence
 * @link https://store.webkul.com/license.html
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
open class BaseModel : BaseObservable() {

    @JsonProperty("success")
   
    var success: Boolean = false

    @JsonProperty("message")
   
    var message: String = ""

    @JsonProperty("eTag")
   @JsonIgnore
    var eTag: String = ""

    @JsonProperty("cartCount")
   
    var cartCount: Int = 0

    @JsonProperty("otherError")
   
    var otherError: String = ""
}