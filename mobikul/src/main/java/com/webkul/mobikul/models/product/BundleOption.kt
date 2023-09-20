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
class BundleOption {

    @JsonProperty("option_id")

    var optionId: String = ""

    @JsonProperty("parent_id")

    var parentId: String = ""

    @JsonProperty("required")

    var required: Int = 0

    @JsonProperty("position")

    var position: String = ""

    @JsonProperty("type")

    var type: String = ""

    @JsonProperty("default_title")

    var defaultTitle: String = ""

    @JsonProperty("title")

    var title: String = ""

    @JsonProperty("optionValues")

    var optionValues: ArrayList<BundleOptionDetails> = ArrayList()
}