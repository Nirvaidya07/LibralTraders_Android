/*
 * Webkul Software.
 *
 * Kotlin
 *
 * @author Webkul <support@webkul.com>
 * @category Webkul
 * @package com.webkul.mobikul
 * @copyright 2010-2019 Webkul Software Private Limited (https://webkul.com)
 * @license https://store.webkul.com/license.html ASL Licence
 * @link https://store.webkul.com/license.html
 */

package com.webkul.mobikul.handlers

import android.content.Intent
import android.view.View
import com.webkul.mobikul.activities.MyReviewListActivity


class DashboardReviewsFragmentHandler {

    fun onClickViewAll(view: View) {
        view.context.startActivity(Intent(view.context, MyReviewListActivity::class.java))
    }
}