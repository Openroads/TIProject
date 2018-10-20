from django.db import models

# Create your models here.


class Document(models.Model):

    title = models.CharField(max_length=255, null=False)

    content = models.CharField(max_length=3500, null=True)

    version = models.IntegerField(null=False)
