package com.webkul.mobikul.models.user


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
class CountryData {
    constructor()

    @JsonProperty(value = "country_id")
    var country_id: String = ""

    @JsonProperty("name")
    var name: String = ""

    @JsonProperty("isStateRequired")
    var isStateRequired: Boolean = false
    @JsonProperty("isZipOptional")
    var isZipOptional: Boolean = false

    @JsonProperty("states")
    var states: ArrayList<State> = ArrayList()
}