/*
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

package com.webkul.mobikul.models.product


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class GroupedData {

    @JsonProperty("name")

    var name: String = ""

    @JsonProperty("id")

    var id: String = ""

    @JsonProperty("isAvailable")

    var isAvailable: Boolean = false

    @JsonProperty("isInRange")

    var isInRange: Boolean = false

    @JsonProperty("specialPrice")

    var specialPrice: String = ""

    @JsonProperty("defaultQty")

    var defaultQty: String = ""

    @JsonProperty("foramtedPrice")

    var formattedPrice: String = ""

    @JsonProperty("thumbNail")

    var thumbNail: String = ""
}