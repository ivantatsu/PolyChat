import json
import boto3

dynamodb = boto3.resource('dynamodb')
table = dynamodb.Table('messages')

def lambda_handler(event, context):
    # Verificar si se proporciona pathParameters y message_id
    if 'message_id' not in event:
        return {
            'statusCode': 400,
            'body': json.dumps({'error': 'Invalid request. Missing message_id'}),
            'event': event
        }

    # Obtener el message_id de los pathParameters
    message_id = event['message_id']

    # Verificar si el mensaje existe en la tabla
    response = table.get_item(Key={'message_id': message_id})
    if 'Item' not in response:
        return {
            'statusCode': 404,
            'body': json.dumps({'error': 'Message not found'})
        }

    # Actualizar los datos del mensaje en la tabla
    table.update_item(
        Key={'message_id': message_id},
        UpdateExpression='SET context = :context, message_type = :message_type, message_status = :message_status, checkUsers = :checkUsers',
        ExpressionAttributeValues={
            ':context': event.get('context', ''),
            ':message_type': event.get('message_type', ''),
            ':message_status': event.get('message_status', ''),
            ':checkUsers': event.get('checkUsers', ''),
        }
    )

    return {
        'statusCode': 200,
        'body': json.dumps({'message': 'Message updated successfully'})
    }
