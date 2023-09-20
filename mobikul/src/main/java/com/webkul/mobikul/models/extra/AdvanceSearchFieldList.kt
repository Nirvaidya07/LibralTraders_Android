package com.webkul.mobikul.models.extra


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
class AdvanceSearchFieldList {

    @JsonProperty("label")

    var label: String = ""

    @JsonProperty("inputType")

    var inputType: String = ""

    @JsonProperty("attributeCode")

    var attributeCode: String = ""

    @JsonProperty("maxQueryLength")

    var maxQueryLength: String = ""

    @JsonProperty("title")

    var title: String = ""

    @JsonProperty("options")

    var options: ArrayList<AdvanceSearchFieldOption> = ArrayList()
}