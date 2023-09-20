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
class LinkData {
    @JsonProperty("id")

    var id: String = ""

    @JsonProperty("linkTitle")

    var linkTitle: String = ""

    @JsonProperty("price")

    var price: Double = 0.0

    @JsonProperty("formattedPrice")

    var formattedPrice: String = ""

    @JsonProperty("url")

    var url: String = ""

    @JsonProperty("fileName")

    var fileName: String = ""

    @JsonProperty("haveLinkSample")

    var haveLinkSample: Int = 0

    @JsonProperty("linkSampleTitle")

    var linkSampleTitle: String = ""
}