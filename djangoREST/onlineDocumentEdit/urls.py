from django.urls import path

from onlineDocumentEdit.views import ListDocumentsView
from onlineDocumentEdit.views import DocumentsDetailView

urlpatterns = [
	path('documents/', ListDocumentsView.as_view()),
	path('document/<int:pk>/', DocumentsDetailView.as_view())
]