from django.contrib.auth.models import User
from django.db import models


# Create your models here.


class Document(models.Model):
    title = models.CharField(max_length=255, null=False)

    content = models.CharField(max_length=3500, null=True)

    version = models.IntegerField(null=False)

    createdBy = models.ForeignKey(User, on_delete=models.DO_NOTHING, related_name='%(class)s_document_created_by',
                                  default=1)

    editingBy = models.ForeignKey(User, on_delete=models.DO_NOTHING, null=True,
                                  related_name='%(class)s_document_editing_by')

    def save(self, *args, **kwargs):
        try:
            self.version = Document.objects.get(pk=self.pk).version + 1
        except Document.DoesNotExist:
            self.version = 1

        super(Document, self).save(*args, **kwargs)
