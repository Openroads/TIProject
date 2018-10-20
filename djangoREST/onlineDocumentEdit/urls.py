from django.urls import path

from onlineDocumentEdit import views
from onlineDocumentEdit.views import ListDocumentsView, DocumentLockView, DocumentUnlockView
from onlineDocumentEdit.views import DocumentsDetailView

urlpatterns = [
	path('documents/', ListDocumentsView.as_view()),
	path('document/<int:pk>/', DocumentsDetailView.as_view()),
	path('document/<int:documentId>/editing-by/<int:userId>/', DocumentLockView.as_view()),
	path('document/<int:documentId>/stop-editing/', DocumentUnlockView.as_view()),
	path('users/', views.UserList.as_view()),
	path('users/<int:pk>/', views.UserDetail.as_view()),
]