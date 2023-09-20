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
class BundleOptionDetails {
    @JsonProperty("title")

    var title: String = ""

    @JsonProperty("isQtyUserDefined")

    var isQtyUserDefined: Int = 0

    @JsonProperty("isDefault")

    var isDefault: String = ""

    @JsonProperty("optionId")

    var optionId: String = ""

    @JsonProperty("optionValueId")

    var optionValueId: Int = 0

    @JsonProperty("foramtedPrice")

    var foramtedPrice: String = ""

    @JsonProperty("price")

    var price: Double? = 0.0

    @JsonProperty("isSingle")

    var isSingle: Boolean = false

    @JsonProperty("defaultQty")

    var defaultQty: Double = 0.0
}