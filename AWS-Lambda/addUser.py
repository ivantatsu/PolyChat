import boto3
import json
from boto3.dynamodb.conditions import Attr

dynamodb = boto3.resource('dynamodb')
table = dynamodb.Table('users')


def lambda_handler(event, context):
    # Obtén el ID de conexión y el cuerpo del mensaje del evento.

    # Si el cuerpo del mensaje incluye un email y un user_id, actualiza el user_id en DynamoDB.
    if 'email' in event and 'user_id' in event:
        email = event['email']
        user_id = event['user_id']

        # Busca el usuario por su correo electrónico
        response = table.scan(
            FilterExpression=Attr('email').eq(email)
        )

        # Verifica si encontró algún elemento
        if 'Items' in response and len(response['Items']) > 0:
            existing_item = response['Items'][0]

            # Actualiza la lista de solicitudes del usuario en DynamoDB
            response = table.update_item(
                Key={
                    'user_id': existing_item['user_id']
                },
                UpdateExpression="ADD requests :new_id",
                ExpressionAttributeValues={
                    ':new_id': set([user_id])
                },
                ReturnValues="UPDATED_NEW"
            )
            if response['ResponseMetadata']['HTTPStatusCode'] != 200:
                print(f"No se pudo actualizar el registro para el email: {email}")
            else:
                response = table.get_item(
                    Key={
                        'user_id': existing_item['user_id']
                    }
                )
                if 'Item' in response:
                    token = response['Item']['token']
        else:
            print(f"No se encontró el email: {email}")

    # Devuelve una respuesta exitosa.
    return {
        'statusCode': 200
    }


