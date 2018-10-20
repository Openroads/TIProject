from django.conf.urls import url
from hello import views
from onlineDocumentEdit.views import ListDocumentsView

urlpatterns = [
	url(r'documents/$', ListDocumentsView.as_view(), name="documents-all"),
	url(r'document/(?P<id>[0-9a-zA-Z]+)/$', views.hello)
]