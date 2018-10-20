from django.contrib.auth.models import User
from rest_framework import serializers
from .models import Document


class DocumentListItemSerializer(serializers.ModelSerializer):
    editingBy = serializers.ReadOnlyField(source='editingBy.username')

    class Meta:
        model = Document
        fields = ("id", "title", "editingBy")


class DocumentDetailsSerializer(serializers.ModelSerializer):
    editingBy = serializers.ReadOnlyField(source='editingBy.username')

    class Meta:
        model = Document
        fields = '__all__'


class UserSerializer(serializers.ModelSerializer):
    # documents = serializers.PrimaryKeyRelatedField(many=True, queryset=Document.objects.all())

    class Meta:
        model = User
        fields = ('id', 'username')