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
class DownloadsList {

    @JsonProperty("incrementId")

    var incrementId: String? = null

    @JsonProperty("isOrderExist")

    var isOrderExist: Boolean = false

    @JsonProperty("message")

    var message: String? = null

    @JsonProperty("hash")

    var hash: String? = null

    @JsonProperty("date")

    var date: String? = null

    @JsonProperty("proName")

    var proName: String? = null

    @JsonProperty("status")
    var status: String? = null

    @JsonProperty("statusColorCode")
    var statusColorCode: String? = null
    get() = field?:"#66BB6A"

    @JsonProperty("state")

    var state: String? = null

    @JsonProperty("remainingDownloads")

    var remainingDownloads: String? = null

    @JsonProperty("canReorder")

    var canReorder: Boolean = false
}