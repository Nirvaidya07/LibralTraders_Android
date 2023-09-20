package com.webkul.mobikul.models


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
class ImageUploadResponseData : BaseModel() {

    @JsonProperty("name")

    var name: String = ""

    @JsonProperty("type")

    var type: String = ""

    @JsonProperty("error")

    var error: Int = 0

    @JsonProperty("size")

    var size: Int = 0

    @JsonProperty("file")

    var file: String = ""

    @JsonProperty("url")

    var url: String = ""
}