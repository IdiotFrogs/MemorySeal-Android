package com.idiotfrogs.friend.component

import com.idiotfrogs.model.timecapsule.TimeCapsuleCollaboratorsResponseData

sealed interface FriendDialogState {
    data object None : FriendDialogState

    data class Delegation(val member: TimeCapsuleCollaboratorsResponseData) : FriendDialogState

    data class Delete(val member: TimeCapsuleCollaboratorsResponseData) : FriendDialogState
}
